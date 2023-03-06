helm package .

helm install --generate-name --values=customvalues.yaml opensearch-2.10.0.tgz

helm list
helm delete <name>

kubectl exec -it opensearch-cluster-master-0 -- /bin/bash
curl -XGET https://localhost:9200 -u 'admin:admin' --insecure

kubectl port-forward deployment/opensearch-dashboards-2-1677594122 5601




