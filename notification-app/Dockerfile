FROM node:12-alpine as builder
WORKDIR /app
COPY notification-app/package*.json /app/
RUN npm install --only=prod --silent
COPY notification-app /app
RUN npm run build --prod

FROM nginx:1.17.10-alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY --from=builder /app/nginx.conf /etc/nginx/nginx.app
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]