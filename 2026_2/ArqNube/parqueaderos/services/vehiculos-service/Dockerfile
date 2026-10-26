# syntax=docker/dockerfile:1
FROM golang:1.26-alpine AS build
WORKDIR /src
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 go build -trimpath -ldflags="-s -w" -o /app ./cmd/server

FROM alpine:3.22
RUN apk add --no-cache wget ca-certificates && addgroup -S app && adduser -S app -G app
COPY --from=build /app /usr/local/bin/vehiculos-service
USER app
EXPOSE 8082
ENTRYPOINT ["/usr/local/bin/vehiculos-service"]
