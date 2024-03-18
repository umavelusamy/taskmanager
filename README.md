# taskmanager


To run postgres using docker 
```
docker run -p 5432:5432 --name postgres -e POSTGRES_PASSWORD=mypassword -d postgres
```

To run [inbucket](https://github.com/inbucket/inbucket) mailserver in localhost
```
docker run --rm --name inbucket -p 9000:9000 -p 2500:2500 -p 1100:1100 inbucket/inbucket:main
```