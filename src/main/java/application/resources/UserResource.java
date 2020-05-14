package application.resources;

import application.core.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
public class UserResource implements IUserResource {

    @Autowired
    private ApplicationConfig appConfig;

    private static Integer counter = 0;
    private Set<String> names = new HashSet<>();

    @GetMapping("hello/{name}")
    public String hello(@PathVariable("name") String name) {
        System.out.println("Service invoked by :" + name);
        StringBuilder response = new StringBuilder();
        ++counter;

        if(names.stream().anyMatch(existingName-> existingName.equals(name))) {
            response.append("Hey you are back ").append(name).append("!! ").append("version: " ).
                    append(appConfig.getVersion()).append(" - ").append(counter);
        } else {
            names.add(name);
            response.append("Hey ").append(name).append("! ").append("version: " )
                    .append(appConfig.getVersion()).append(" - ").append(counter);
        }

        return response.toString();
    }

}
