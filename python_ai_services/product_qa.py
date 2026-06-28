import os
import json
from fastapi import HTTPException
from pydantic import BaseModel
from typing import Optional, List
from groq import Groq
from dotenv import load_dotenv

load_dotenv()
 
GROQ_API_KEY=os.getenv("GROQ_API_KEY")

if not GROQ_API_KEY:
    raise RuntimeError("GROQ_API_KEY not set in .env file")

client=Groq(api_key=GROQ_API_KEY)

class ReviewContext(BaseModel):
    rating: int
    comment: str

class ProductQARequest(BaseModel):
    productId: int
    productName: str
    productDescription: str
    brand: str
    category: str
    actualPrice: int
    discountedPrice: int
    discountPercent: int
    reviews: Optional[List[ReviewContext]] = []
    question: str
    chatHistory: Optional[List[dict]] = []

class ProductQAResponse(BaseModel):
    answer: str
    productId: int

def build_system_prompt(req: ProductQARequest) -> str:
    reviews_text = ""
    if req.reviews:
        reviews_text = "\n".join(
            f"- Rating {r.rating}/5: {r.comment}"
            for r in req.reviews if r.comment
        )
    else:
        reviews_text = "No reviews available yet."
 
    return f"""
You are a helpful product assistant for an ecommerce platform.
A customer is asking questions about a specific product. Answer ONLY based on the product details provided below.
If the answer is not in the product details or reviews, say "I don't have that information for this product."
Keep answers short, friendly, and helpful — 1 to 3 sentences max.
Do NOT make up specifications that are not mentioned.
 
Product Details:
- Name: {req.productName}
- Brand: {req.brand}
- Category: {req.category}
- Actual Price: ₹{req.actualPrice}
- Discounted Price: ₹{req.discountedPrice}
- Discount: {req.discountPercent}%
- Description: {req.productDescription}
 
Customer Reviews:
{reviews_text}
"""

async def answer_product_question(req: ProductQARequest) -> ProductQAResponse:
    system_prompt = build_system_prompt(req)
 
    messages = [{"role": "system", "content": system_prompt}]
 
    for msg in req.chatHistory:
        if msg.get("role") in ("user", "assistant") and msg.get("content"):
            messages.append({"role": msg["role"], "content": msg["content"]})
 
    messages.append({"role": "user", "content": req.question})
 
    try:
        response = client.chat.completions.create(
            model="llama-3.3-70b-versatile",
            messages=messages,
            temperature=0.3
        )
        answer = response.choices[0].message.content.strip()
        return ProductQAResponse(answer=answer, productId=req.productId)
 
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Q&A error: {str(e)}")
