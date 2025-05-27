# Stage 1: Build the React application
FROM node:20-alpine AS build
WORKDIR /app

# Declare the build argument that can be passed during `docker build`
ARG VITE_API_URL="http://localhost:8080"

# Set the build argument as an environment variable for the build process
# Vite will pick up VITE_ prefixed env vars during the build
ENV VITE_API_URL=${VITE_API_URL}

# Copy package.json and package-lock.json (or yarn.lock)
COPY package.json package-lock.json ./

# Install dependencies
RUN npm install

COPY . .
RUN ls -al /app/src/api
RUN npm run build
# add this just before your `npm run build` in Dockerfile


# Stage 2: Serve the static files with Nginx
FROM nginx:1.25-alpine
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf
# Copy the built static files from the build stage to the Nginx html directory
COPY --from=build /app/dist /usr/share/nginx/html



# Expose port 80 (Nginx default)
EXPOSE 80

# Command to run Nginx in the foreground
CMD ["nginx", "-g", "daemon off;"]