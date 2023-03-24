FROM daprio/daprd:edge
COPY . .

CMD ["./daprd", "-app-id", "java-api", "--app-port", "3002","-placement-host-address", "placement:50006","-components-path", "/components", "--config", "/config.yaml"]