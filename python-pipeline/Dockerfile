FROM python:3.12-slim

WORKDIR /app

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY . .

# Expose port for any HTTP server if needed
EXPOSE 8000

# Default command to run the prototype prototype
CMD ["python", "run_prototype.py"]