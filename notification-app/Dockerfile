FROM node:15.11.0-alpine3.13 as builder
WORKDIR /app
COPY notification-app/package*.json /app/
RUN npm install --only=prod --silent
COPY notification-app /app
RUN npm run build --prod

FROM node:15.11.0-alpine3.13 as builder
COPY --from=builder /app/dist /usr/share/nginx/html
COPY --from=builder /app/nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]