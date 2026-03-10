FROM node:16

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install

COPY . .

COPY .env .env

EXPOSE 3001

CMD ["npm", "start"]
