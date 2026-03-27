#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
AutoBook AI — FastAPI microservice
Run: uvicorn main:app --reload --port 8000
Docs:   http://localhost:8000/docs
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from fastapi.responses import FileResponse

from routers import analyze, suggest, generate, vocabulary, consistency

app = FastAPI(
    title="AutoBook AI",
    description="""
AI service for analyzing author writing style and assisting during the writing process.

## Endpoints

### /analyze
Analyzes author texts → builds a style profile (TF-IDF + metrics).

### /suggest
Suggests words and phrases while writing based on the author's style.

### /generate
Generates text continuation in the author's style via Ollama.

### /vocabulary
Public author vocabulary — characteristic words with meanings.

### /consistency
Checks how closely a chapter matches the author's style profile.
    """,
    version="2.1.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(analyze.router,      prefix="/api/v1/analyze",      tags=["Style Analysis"])
app.include_router(suggest.router,      prefix="/api/v1/suggest",      tags=["Suggestions"])
app.include_router(generate.router,     prefix="/api/v1/generate",     tags=["Text Generation"])
app.include_router(vocabulary.router,   prefix="/api/v1/vocabulary",   tags=["Vocabulary"])
app.include_router(consistency.router,  prefix="/api/v1/consistency",  tags=["Consistency"])


@app.get("/health", tags=["System"])
def health():
    return {"status": "ok", "service": "autobook-ai", "version": "2.1.0"}


app.mount("/static", StaticFiles(directory="static"), name="static")


@app.get("/", tags=["System"])
def root():
    return FileResponse("static/index.html")