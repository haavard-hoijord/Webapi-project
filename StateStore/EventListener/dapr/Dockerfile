FROM daprio/daprd:edge
COPY . .

CMD ["./daprd", "-app-id", "event-listener", "--app-port", "3001","-placement-host-address", "placement:50006","-components-path", "/components", "--config", "/config.yaml"]