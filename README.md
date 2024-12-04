# Webapi-project

Webapi-project is a learning project designed to explore microservices architecture and the use of **Dapr** for building distributed applications. The project implements a simple set of services to demonstrate concepts like service communication, state management, and observability with Dapr.

## Table of Contents

- [About](#about)
- [Features](#features)
- [Installation](#installation)
  - [Prerequisites](#prerequisites)
  - [Setup](#setup)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## About

This project focuses on learning and applying microservices principles with the **Dapr** framework. It provides hands-on experience in integrating Dapr for service-to-service communication, state management, and observability in a .NET-based environment.

## Features

- **Microservices Architecture**: Demonstrates decoupled services that communicate via Dapr.
- **Dapr Integration**: Uses Dapr for service invocation, pub/sub messaging, and state management.
- **Scalability**: Designed with scalability in mind, allowing easy extension with additional services.
- **Observability**: Leverages Dapr's built-in tools for logging, metrics, and tracing.

## Installation

### Prerequisites

- **.NET SDK**: Ensure the appropriate version of the .NET SDK is installed.
- **Dapr CLI**: Install Dapr CLI to run and manage Dapr-enabled applications. See the [Dapr installation guide](https://docs.dapr.io/getting-started/install-dapr-cli/).
- **Docker**: Required for running Dapr sidecars and state management components (optional but recommended).

### Setup

1. **Clone the Repository**:

   ```bash
   git clone https://github.com/haavard-hoijord/Webapi-project.git
   cd Webapi-project
   ```

2. **Install Dapr**:

   Follow the [official Dapr installation guide](https://docs.dapr.io/getting-started/) to install and initialize Dapr on your machine.

3. **Restore Dependencies**:

   ```bash
   dotnet restore
   ```

4. **Run Dapr Sidecar**:

   Start a Dapr sidecar for each service using:

   ```bash
   dapr run --app-id service-name --app-port 5000 -- dotnet run
   ```

   Replace `service-name` with the appropriate service identifier.

## Usage

1. **Start Services**:

   Run the services using the commands provided in the setup section. Each service will register with Dapr and become discoverable.

2. **Interact with Services**:

   Use tools like Postman or curl to invoke endpoints. Services will communicate via Dapr's service invocation feature.

3. **Explore State Management**:

   Test state persistence using Dapr's state management API. Modify the service code to leverage Dapr's state store for storing and retrieving data.

4. **Monitor Logs and Metrics**:

   Utilize Dapr's observability features to monitor service logs and metrics.

## Contributing

As this repository is archived and read-only, contributions are no longer accepted. However, feel free to fork the repository for personal exploration and learning.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.
