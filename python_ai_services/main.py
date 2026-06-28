import os
import json
import tempfile
from fastapi import FastAPI,HTTPException,UploadFile,File
from pydantic import BaseModel
from typing import List,Optional
from groq import Groq
from dotenv import load_dotenv
from product_qa import ProductQARequest, ProductQAResponse, answer_product_question

load_dotenv()

GROQ_API_KEY=os.getenv("GROQ_API_KEY")

if not GROQ_API_KEY:
    raise RuntimeError("GROQ_API_KEY not set in .env file")

client=Groq(api_key=GROQ_API_KEY)

app=FastAPI(title="Review Summary Service")

class ReviewItem(BaseModel):
    rating: int
    comment: str

class SummaryRequest(BaseModel):
    productId: int
    reviews: List[ReviewItem]

class SearchFilters(BaseModel):
    name: Optional[str]=None
    category: Optional[str]=None
    brand:Optional[str]=None
    minPrice:Optional[int]=None
    maxPrice:Optional[int]=None


def build_prompt(reviews:List[ReviewItem])-> str:
    review_lines="\n".join(
        f"- Rating: {r.rating}/5 | Comment: {r.comment}"
        for r in reviews
    )

    return f"""
You are a product review analyst. Below are customer reviews for a product.
 
Reviews:
{review_lines}
 
Write a concise 2-3 sentence summary of these reviews. Your summary must:
1. First mention what customers liked (based on positive reviews, rating 4-5).
2. Then mention what customers disliked or complained about (based on negative reviews, rating 1-2).
3. End with a concluding line about the overall customer experience (do NOT mention average rating or numbers, just describe the general feeling customers have about the product).. 
 
Keep it natural, like a paragraph — not bullet points. Be specific and refer to actual comments.

Here are examples of good summaries:

Example 1:
Reviews: great build quality, fast shipping, love the design | bad battery life, stopped working after a week
Summary: "Customers loved the product's solid build quality, fast shipping, and attractive design. On the downside, several users raised concerns about poor battery life and durability issues. Most buyers enjoy the product initially but feel it could be more reliable in the long run."

Example 2:
Reviews: very comfortable, true to size, good material | color faded after one wash, stitching came loose
Summary: "Shoppers appreciated the comfort, accurate sizing, and quality material of this product. However, a few customers were disappointed by the color fading after the first wash and stitching that came apart quickly. While the product makes a great first impression, its long-term quality leaves room for improvement."

Example 3:
Reviews: excellent customer support, easy to use, affordable | manual is confusing, missing some features
Summary: "Buyers highlighted the excellent customer support, ease of use, and affordable price as major positives. Some users found the manual confusing and felt the product lacked certain expected features. Overall, customers find it a good value for money despite a few minor shortcomings."

Now write a similar summary for the reviews above. Do not copy the examples — use them only as a guide for tone and structure.

"""

def transcribe_audio(audio_bytes: bytes,filename:str)-> str:
    suffix=os.path.splitext(filename)[-1] or ".mp3"
    with tempfile.NamedTemporaryFile(suffix=suffix,delete=False) as tmp:
        tmp.write(audio_bytes)
        tmp_path=tmp.name
    try:
        with open(tmp_path,"rb") as audio_file:
            response=client.audio.transcriptions.create(model="whisper-large-v3",
                                                        file=(filename,audio_file),language="en")
        return response.text.strip()
    finally:
        os.unlink(tmp_path)

def parse_query_to_filters(query: str) -> SearchFilters:
    prompt = f"""
Extract product search filters from the user's voice query.

Output ONLY a valid JSON object.
Do NOT include explanations, markdown, or backticks.

Use ONLY these allowed categories:
- electronics
- footwear
- clothing
- furniture
- appliances
- skincare
- fitness
- jewellery
- sports
- baby products

Category mapping rules:
- mobile, smartphone, phone, laptop, headphones, earbuds, printer → electronics
- shoes, sneakers, sandals, boots → footwear
- tshirt, jeans, hoodie, shirt → clothing
- chair, table, sofa, bed → furniture
- refrigerator, washing machine, microwave → appliances
- face wash, cream, shampoo → skincare
- protein powder, dumbbells → fitness
- necklace, ring, gold chain → jewellery
- cricket bat, football, badminton racket → sports
- diapers, baby lotion, baby soap → baby products

JSON format:
{{
  "name": "product keyword or null",
  "category": "one allowed category or null",
  "brand": "brand if mentioned or null",
  "minPrice": number or null,
  "maxPrice": number or null
}}

Examples:

Query: "show me Samsung mobile under 20000"
Output:
{{"name":"mobile","category":"electronics","brand":"Samsung","minPrice":null,"maxPrice":20000}}

Query: "find me a laptop"
Output:
{{"name":"laptop","category":"electronics","brand":null,"minPrice":null,"maxPrice":null}}

Query: "show me Nike shoes under 3000"
Output:
{{"name":"shoes","category":"footwear","brand":"Nike","minPrice":null,"maxPrice":3000}}

Query: "show me t-shirts under 500"
Output:
{{"name":"t-shirt","category":"clothing","brand":null,"minPrice":null,"maxPrice":500}}

Query: "find me a wooden dining table"
Output:
{{"name":"dining table","category":"furniture","brand":null,"minPrice":null,"maxPrice":null}}

Query: "show me LG washing machine"
Output:
{{"name":"washing machine","category":"appliances","brand":"LG","minPrice":null,"maxPrice":null}}

Query: "show me face wash under 200"
Output:
{{"name":"face wash","category":"skincare","brand":null,"minPrice":null,"maxPrice":200}}

Query: "find me protein powder"
Output:
{{"name":"protein powder","category":"fitness","brand":null,"minPrice":null,"maxPrice":null}}

Query: "show me gold jewellery under 50000"
Output:
{{"name":"jewellery","category":"jewellery","brand":null,"minPrice":null,"maxPrice":50000}}

Query: "I want a cricket bat under 2000"
Output:
{{"name":"cricket bat","category":"sports","brand":null,"minPrice":null,"maxPrice":2000}}

Query: "show me baby diapers"
Output:
{{"name":"diapers","category":"baby products","brand":null,"minPrice":null,"maxPrice":null}}


Query: "{query}"
Output:"""

    response = client.chat.completions.create(
        model="llama-3.3-70b-versatile",
        messages=[{"role": "user", "content": prompt}],
        temperature=0
    )
    raw = response.choices[0].message.content.strip()
    start = raw.find("{")
    end = raw.rfind("}")
    if start == -1 or end == -1:
        raise ValueError(f"No JSON found in response: {raw}")
    data = json.loads(raw[start:end + 1])
    return SearchFilters(**data)

@app.post("/summarize")
async def summarize_reviews(request: SummaryRequest)->str:
    if not request.reviews:
        raise HTTPException(status_code=400,detail="No reviews provided")
    
    prompt=build_prompt(request.reviews)

    try:
        response=client.chat.completions.create(
            model="llama-3.3-70b-versatile",
            messages=[
                {"role":"user","content": prompt}
            ]
        )
        summary=response.choices[0].message.content.strip()
        return summary
    except Exception as e:
        raise HTTPException(status_code=500,detail=f"Gemini API error: {str(e)}")
    
@app.post("/voice-search", response_model=SearchFilters)
async def voice_search(audio: UploadFile = File(...)):
    allowed = {".mp3", ".mp4", ".wav", ".m4a", ".webm", ".ogg", ".flac"}
    ext = os.path.splitext(audio.filename)[-1].lower()
    if ext not in allowed:
        raise HTTPException(status_code=400, detail=f"Unsupported file type '{ext}'")
    try:
        audio_bytes = await audio.read()
        transcript = transcribe_audio(audio_bytes, audio.filename)
        filters = parse_query_to_filters(transcript)
        return filters
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Voice search error: {str(e)}")
    
@app.post("/product-qa", response_model=ProductQAResponse)
async def product_qa(request: ProductQARequest) -> ProductQAResponse:
    if not request.question or not request.question.strip():
        raise HTTPException(status_code=400, detail="Question cannot be empty")
    return await answer_product_question(request)


@app.get("/health")
def health():
    return {"status": "ok", "service": "review-summary"}