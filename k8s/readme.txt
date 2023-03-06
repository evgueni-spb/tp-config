//Helm upgrade



helm template ./config-poc
helm install config-poc ./config-poc
helm ls --all

helm upgrade config-poc-1 config-poc

kubectl exec -it config-poc-1-7f4dd8785-hqqqx -- /bin/sh

minikube -p config tunnel

curl -X POST -H "Content-Type: application/json" -d '{}' http://localhost:8090/change-secret