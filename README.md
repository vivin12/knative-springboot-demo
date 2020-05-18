# knative-springboot-demo

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
    
## Steps to create knative service using yaml files

Skip the first 2 steps if you have already logged in and created a project in OCP.

1. Login to your OCP cluster from terminal. 
2. Create a new project 
   
   `oc new-project knative-demo`

3. Now let’s try to create the service using a yaml files. Let’s apply the first yaml which creates revision 1 of the app.
   
  `oc apply -f service-v1.yaml`

4. Now apply the yaml file `service-v2.yaml`which will create revision 2 and by default all of the traffic will be routed to revision 2.
