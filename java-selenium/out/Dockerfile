FROM php:8.1-apache

# Install system dependencies
RUN apt-get update && apt-get install -y \
    unzip \
    wget \
    libzip-dev \
    libpng-dev \
    libjpeg-dev \
    libfreetype6-dev \
    && docker-php-ext-configure gd --with-freetype --with-jpeg \
    && docker-php-ext-install gd zip mysqli pdo pdo_mysql \
    && a2enmod rewrite \
    && rm -rf /var/lib/apt/lists/*

# Download OpenCart
WORKDIR /var/www/html
RUN wget https://github.com/opencart/opencart/releases/download/4.0.2.3/opencart-4.0.2.3.zip \
    && unzip opencart-4.0.2.3.zip \
    && mv opencart-4.0.2.3/upload/* . \
    && rm -rf opencart-4.0.2.3 opencart-4.0.2.3.zip

# Permissions
RUN chown -R www-data:www-data /var/www/html

EXPOSE 80
