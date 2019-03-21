# Overview

jv is a simplistic, in-memory KV store that implements the following REST interface:

|URI    |Method |Description|
|-------|-------|-----------|
|``/objects/<key>``|DELETE|Deletes the object identified by ``<key>`` and returns the value of the deleted object.|
|``/objects/<key>``|GET|Returns the value of the object identified by ``<key>``|
|``/objects/<key>``|PUT|Adds or replaces the object identified by ``<key>``.  Updates to a key are last write wins.|

# Building jv

In order to build jv, JDK 8 and Docker client tools must be installed on the machine.  Docker images are deployed to the repository configured for the local Docker client installation.  When deploying the minikube, starting minikube as follows with a private, insecure registry is advised for a seamless development workflow:

```
minikube start --insecure-registry 10.0.0.0/24
```

Once started, execute the following command in the shell to configure the Docker client to reuse the Docker instance in the minikube VM:

```
eval $(minikube docker-env)
```

The following command will build jv and publish a Docker image:

```
gradle clean jar docker
```

# Running jv

jv is deployed to a Kubernetes cluster a NodePort service listening on port 30080.  To deploy it to a cluster, ensure that the kubectl has been properly configured to access the cluster and execute the following command from the root directory of the project:

```
kubectl create -f jv-kube-config.yaml
```

If jv is deployed to minikube, the URL for the endpoint can be access in the following manner:

```
curl http://$(minikube ip):30080/
```
