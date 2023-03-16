FROM daprio/daprd:edge
COPY . .

CMD ["./daprd", "-app-id", "server", "--app-port", "3000","-placement-host-address", "placement:50006","-components-path", "/components", "--config", "/config.yaml"]