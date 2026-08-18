# health-hazard-events


local dev
docker-compose up
build via intellij

1. start minikube
   minikube start
2. kubectl cluster-info
# This should cleanly display your Minikube control plane network coordinates
kubectl cluster-info

# Ensure you see your node reporting a "Ready" state
kubectl get nodes





# 1. Build your Spring application fat jar
./gradlew clean bootJar

# 2. Build the app image and spin up the entire cluster simultaneously
docker compose up --build

# 3. Tear down the environment when you are done testing
docker compose down

----

# 1. Lint and validate the templates using both configuration files
helm lint ./spring-boot-cron-chart -f ./spring-boot-cron-chart/values.yaml -f ./spring-boot-cron-chart/value-secrets.yaml

# 2. Render and debug the compiled manifest to make sure it looks right
helm template my-release ./spring-boot-cron-chart -f ./spring-boot-cron-chart/values.yaml -f ./spring-boot-cron-chart/value-secrets.yaml --debug

# 3. Deploy the application package to your cluster
helm upgrade --install data-sync-pipeline ./spring-boot-cron-chart --namespace local --create-namespace -f ./spring-boot-cron-chart/values.yaml -f ./spring-boot-cron-chart/value-secrets.yaml

helm upgrade --install health-hazard-tracker-local ./charts -n local --set apiKeys.thirdPartyKey="$(grep FDA_API_KEY .env | cut -d '=' -f2)"
OR
helm upgrade health-hazard-tracker-local ./charts -n local --reuse-values


# 4. Trigger an immediate execution manually to verify end-to-end functionality
kubectl create job --from=cronjob/data-sync-pipeline-sync-job manual-test-run -n local



┌────────────────────────────────────────────────────────────────────────┐
│                          YOUR WORKSTATION                              │
│                                                                        │
│  [ ENVIRONMENT A: Local Compose ]      [ ENVIRONMENT B: Kubernetes ]   │
│  ┌─────────────────────────────┐      ┌─────────────────────────────┐  │
│  │   docker compose up         │      │  kubectl create job         │  │
│  │  ┌───────────────────────┐  │      │  ┌───────────────────────┐  │  │
│  │  │ Spring Boot (Local)   │  │      │  │ Spring Boot (In Pod)  │  │  │
│  │  └───────────────────────┘  │      │  └───────────────────────┘  │  │
│  └──────────────┬──────────────┘      └──────────────┬──────────────┘  │
│                 │ (Connects to)                      │ (Connects to)   │
│                 ▼                                    ▼                 │
│         ┌──────────────┐                     ┌──────────────┐          │
│         │ Kafka Broker │                     │ Kafka Broker │          │
│         └──────────────┘                     └──────────────┘          │
└────────────────────────────────────────────────────────────────────────┘

[Main Thread] ──► Packs RAM Buffer
│
[Sender Thread] ──(Groups into compressed batches)──► Ships via TCP
│
[Kafka Broker]
(Writes to Disk)
│
[Atomic Counters] ◄── [Thread Pool Pool] ◄──(Fires independent Acks)─────┘