import os
from fastapi import FastAPI,HTTPException
from pydantic import BaseModel
from typing import List
from groq import Groq
from dotenv import load_dotenv

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
    
@app.get("/health")
def health():
    return {"status": "ok", "service": "review-summary"}

