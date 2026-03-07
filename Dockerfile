# Naca COBOL Development Environment
# Includes GnuCOBOL compiler and JDK 21 for running transpiled Java
FROM ubuntu:22.04

LABEL maintainer="naca@example.com"
LABEL description="COBOL to Java transpilation environment with GnuCOBOL and JDK 21"

# Prevent interactive prompts during installation
ENV DEBIAN_FRONTEND=noninteractive

# Install system dependencies
RUN apt-get update && apt-get install -y \
    gnucobol4 \
    openjdk-21-jdk \
    gradle \
    && rm -rf /var/lib/apt/lists/*

# Set JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Verify installations
RUN java -version && \
    cobc --version && \
    gradle --version

# Create working directory
WORKDIR /app

# Copy project files
COPY . /app/

# Set default command
CMD ["/bin/bash"]