# Java RAG System

A Java-based Retrieval-Augmented Generation (RAG) system that combines document ingestion, vector storage, and language model inference to provide contextual question-answering capabilities.

## Overview

This project implements a complete RAG pipeline using:
- **Document Ingestion**: Loads and processes text documents
- **Vector Storage**: Uses Milvus for storing document embeddings
- **Embedding Generation**: Python-based API using SentenceTransformer models
- **Language Model**: Integration with Ollama for text generation
- **Retrieval System**: Vector similarity search for relevant context

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Document      │    │   Embedding     │    │   Vector        │
│   Ingestion     │───▶│   Generation    │───▶│   Storage       │
│                 │    │   (Python API)  │    │   (Milvus)      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                        │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Answer        │    │   Language      │    │   Context       │
│   Generation    │◀───│   Model         │◀───│   Retrieval     │
│                 │    │   (Ollama)      │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Features

- **Multi-format Document Processing**: Load and process text documents
- **Semantic Search**: Vector-based similarity search using embeddings
- **Context-aware Responses**: Generate answers based on retrieved relevant context
- **Modular Design**: Separate components for ingestion, retrieval, and generation
- **External LLM Integration**: Uses Ollama for language model inference
- **Scalable Vector Storage**: Milvus database for efficient vector operations

## Prerequisites

### Java Environment
- Java 11 or higher
- Maven 3.6+

### External Services
- **Milvus**: Vector database (default: localhost:19530)
- **Ollama**: Language model server (default: localhost:11434)
- **Python Embedding API**: SentenceTransformer service (default: localhost:5005)

## Installation

### 1. Clone the Repository
```bash
git clone <repository-url>
cd Java-Rag-System
```

### 2. Install Java Dependencies
```bash
mvn clean install
```

### 3. Set up Python Embedding Service
```bash
cd src/embedding-api
pip install flask sentence-transformers
python embedding_api.py
```

### 4. Install and Start Milvus
Follow the [Milvus installation guide](https://milvus.io/docs/install_standalone-docker.md) or use Docker:
```bash
docker run -d --name milvus -p 19530:19530 milvusdb/milvus:latest
```

### 5. Install and Start Ollama
- Install Ollama from [https://ollama.ai](https://ollama.ai)
- Pull the required model:
```bash
ollama pull gemma3:1b
```

## Usage

### 1. Document Ingestion
First, run the ingestion process to load documents into the vector database:
```bash
mvn exec:java -Dexec.mainClass="ingestion.App"
```

### 2. Run RAG Application
Start the main RAG application:
```bash
mvn exec:java -Dexec.mainClass="LLM.AppRag"
```

### 3. Ask Questions
The system will prompt you to enter questions, and it will:
1. Retrieve relevant context from the vector database
2. Generate contextual answers using the language model

## Project Structure

```
src/
├── main/java/
│   ├── ingestion/           # Document processing and vector storage
│   │   ├── App.java         # Main ingestion application
│   │   ├── SimpleDocumentLoader.java
│   │   ├── RemoteEmbedder.java
│   │   ├── MilvusVectorStore.java
│   │   └── MilvusConnection.java
│   ├── retrieval/           # Vector search and context retrieval
│   │   └── VectorRetriever.java
│   └── LLM/                # Language model integration
│       ├── AppRag.java      # Main RAG application
│       └── RagPipeline.java # RAG pipeline orchestration
├── embedding-api/           # Python embedding service
│   └── embedding_api.py
└── resources/
    └── doc1.txt            # Sample documents
```

## Configuration

### Language Model Settings
- **Model**: gemma3:1b (configurable in RagPipeline.java)
- **Temperature**: 0.2
- **Base URL**: http://localhost:11434

### Vector Database Settings
- **Host**: 127.0.0.1
- **Port**: 19530
- **Collection**: Automatically managed

### Embedding API Settings
- **Model**: all-MiniLM-L6-v2
- **Endpoint**: http://127.0.0.1:5005/embed

## Dependencies

### Java (Maven)
- **LangChain4J**: Framework for LLM applications
- **Ollama Integration**: Language model client
- **Milvus SDK**: Vector database client
- **Gson**: JSON processing

### Python
- **Flask**: Web framework for embedding API
- **SentenceTransformers**: Pre-trained embedding models

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Troubleshooting

### Common Issues
- **Connection refused**: Ensure all external services (Milvus, Ollama, Python API) are running
- **Model not found**: Make sure to pull the required Ollama model: `ollama pull gemma3:1b`
- **Port conflicts**: Check if default ports (19530, 11434, 5005) are available

### Support
For issues and questions, please create an issue in the repository.
