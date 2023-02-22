//build docker image
docker build -t coupdevent/app:0.0.1 .

//push to dockerhub
docker push coupdevent/app:0.0.1
