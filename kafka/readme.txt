//install strimzi
kubectl create namespace kafka
kubectl create -f ./strimzi-install.yaml -n kafka

//create cluster
kubectl apply -f ./kafka-persistent-single.yaml -n kafka
kubectl wait kafka/my-cluster --for=condition=Ready --timeout=300s -n kafka

//produce message
kubectl -n kafka run kafka-producer -ti --image=quay.io/strimzi/kafka:0.33.2-kafka-3.4.0 --rm=true --restart=Never -- bin/kafka-console-producer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic my-topic

//consume message
kubectl -n kafka run kafka-consumer -ti --image=quay.io/strimzi/kafka:0.33.2-kafka-3.4.0 --rm=true --restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic my-topic --from-beginning

//get list of topics
kubectl -n kafka run kafka-topics -ti --image=quay.io/strimzi/kafka:0.33.2-kafka-3.4.0 --restart=Never -- bin/kafka-topics.sh --list --bootstrap-server my-cluster-kafka-bootstrap:9092

