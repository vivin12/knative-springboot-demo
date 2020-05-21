# knative-springboot-demo

This project was createad using appsody stack which is a developer tool that comes with cloud paks for application product by IBM. The first part "Steps to initialize a project using appsody" is just for folks interested in creating app with appsody stack from scratch. Skip over to "Prerequisites" if you just want to create a knative service. 

## Steps to initialize a project using appsody 

To install appsody CLI follow instructions for your OS - https://appsody.dev/docs/installing/installing-appsody/
Follow these steps if you have access to appsody stack and you would like to initialize project using appsody.

1. To check the repo's available for you 
   
   `appsody repo list`

2. To remove a repo 
   
   `appsody repo remove <NAME>`

3. To check all the available templates do  

   `appsody list`
    
4. The repo with `*` before it's name is the selected one and if you to initialize a project from a template that is in a   different repo you need to switch to that repo. To do that run the following command. 
   
   ```
    appsody repo set-default <repo-name>
    eg: appsody repo set-default kabanero
   ```
   
5. To initialize a project using a template create the project directory and run the following command.
   
   ```
    mkdir knative-springboot
    appsody init java-spring-boot2 
   ```
6. To build your project do the below command and it will locally build a docker image of your appsody project. It helps you to check that stack is stable and init is done correctly. You do not need to run build directly ever again

   ```
    cd knative-springboot
    appsody build
   ```
7. To run the project do
   
   `appsody run`

   Open the application using the web browser at `http://localhost:8080`

   By default, the template provides the below endpoints:
     ```
        Health endpoint: http://localhost:8080/actuator/health

        Liveness endpoint: http://localhost:8080/actuator/liveness

        Metrics endpoint: http://localhost:8080/actuator/metrics

        Prometheus endpoint: http://localhost:8080/actuator/prometheus
     ```

For more details, refer - `https://github.com/appsody/stacks/blob/master/incubator/java-spring-boot2/README.md`

Now you are all set to start building your app. 

## Prerequisites

A k8’s cluster with knative serving installed on it. If you have an open shift cluster without knative installed follow the instructions on the below link to install the knative operator.

`https://access.redhat.com/documentation/en-us/openshift_container_platform/4.3/html/serverless_applications/installing-openshift-serverless-1#installing-openshift-serverless`

Once you have that ready we are good to proceed. 

## CLI’s to download 

we use the below CLI's in this demo. Follow download instructions for your OS. 

1. Hey  -  used to send load to your web application. 

   `https://github.com/rakyll/hey`
2. HTTPie - Used to interact with HTTP servers. 

   `https://httpie.org/docs#installation`
3. Knative CLI. 
   Download the latest version of the client from the following link.
   
   `https://github.com/knative/client/releases/tag/v0.12.0`
    
    After you download the file you can rename “Kn-darwin-amd64” to “kn”it make it executable and add it to your PATH.

## Steps to create knative service using KN client 

1. Login to your OCP cluster from terminal
2. Create a new project 
   
   `oc new-project knative-demo`
3. We will use the kn client to deploy our springnboot app
   
   `kn service create knative-springboot --namespace knative-demo --image gooner4life/knative-springboot:v1`
4. Execute below commands and check for the k8’s resources that was created 
   ``` 
   kn revision list 
   Kn service list 
   Kn route list
   ```
   If you execute `oc get pods` you will see that the initial pod that got created got terminated. That is because by default knative services are scaled down to zero. You can always change that to fit your use case. 
5. Now let’s invoke the REST service in the app to see what happens.
   To find your route you can do : `kn route list`
   
   Or  `oc get rt knative-springboot -o yaml | yq read - 'status.url'`

   http GET {your route}/hello/{name}
   Eg : http GET http://knative-springboot/hello/stranger

   Do `oc get pods` and you will see that 1 pod got created. If you don’t invoke the REST api again the pod will start terminating after 60 secs. We will talk about increasing/decreasing this time later in this tutorial.  

6. Let’s create a new revision of the app using kn client. All we are doing here is passing an application property as an environment variable. This will create a new revision and all of the traffic will be routed to the new revision which is the default behavior. 
  
   `kn service update knative-springboot --env "version=v2”`
   
 7. Let’s clean up everything. Below command will delete all the resources associated with the knative service that you created.

    `kn service delete knative-springboot` 
    
## Steps to create knative service using appsody

Folks with access to appsody stack can follow the below instructions to create a knative service using appsody CLI. We assume that you used appsody to initialize the project. 

1. Run appsody build which will create a docker image on you local machine. 

   ```
   cd knative-springboot
   appsody build
   ```
2. You can push the image to your dockerhub or another registry and then use that image to deploy to your Openshift cluster as a knative service using appsody. Using the --knative flag with the appsody build or appsody deploy commands sets the flag createKnativeService in the deployment manifest to true.
Note that you need to configure your cluster to have access to pull from whatever registry you are using.
 
    `appsody deploy -t <username/repository[:tag]> --push --namespace mynamespace [--knative]`
    
    Alternatively you can just use the image we have on dockerhub so you can skip the steps of building and pushing the image.

   `appsody deploy -t gooner4life/appsody-springboot:v1 --namespace <your namespace> --knative`
   
3. Check to verify that kn service got created.
  
  `kn service list`
 
## Steps to create knative service using yaml files

Skip the first 2 steps if you have already logged in and created a project in OCP.

1. Login to your OCP cluster from terminal. 
2. Create a new project 
   
   `oc new-project knative-demo`

3. Now let’s try to create the service using a yaml files. Let’s apply the first yaml which creates revision 1 of the app.
   
  `oc apply -f service-v1.yaml`

4. Now apply the yaml file `service-v2.yaml`which will create revision 2 and by default all of the traffic will be routed to revision 2.

5. Let's switch back the traffic to revision 1.
