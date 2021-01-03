package org.whitehat.httpworkshop;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class ApprenticeController {

    private static Map<String, Apprentice> apprenticesHashMap = new HashMap<>();
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String gettingStarted() {
        return "Welcome. You have made your first successful GET request!\nTo continue through this HTTP request challenge I will ask you to make a series of HTTP requests.\nOnce you have completed them all you will unlock a special reward.\n\nTo get started can you make a POST request to `/apprentices`.\n Include your name in the body of the request as a stringified JSON object with the property 'name'.";
    }

    @PostMapping("/apprentices")
    @ResponseStatus(HttpStatus.CREATED)
    String createApprenticeEntry(@RequestBody Apprentice apprentice, HttpServletResponse res) {
        String hashkey = apprentice.toString().split("@")[1];
        
        apprenticesHashMap.put(hashkey, apprentice);

        res.addHeader("Your-Id", hashkey);
        
        return String.format("Hi %s, can you see in the headers of this response I have placed a field called 'Your-Id'.\n\nCan you make a GET request to '/apprentices/{Your-Id}' with 'Your-Id' set as a parameter?", apprentice.getName());
    }

    @RequestMapping("/apprentices/{hash}")
    String withParams(@PathVariable String hash, HttpServletResponse res) {
        try {
            Apprentice apprentice = apprenticesHashMap.get(hash);
            return String.format("Nice work %s, I can see you know what you are doing! Keep using 'Your-Id' as a parameter.\nI am a stateless server so everything I need to talk to you has to be contained in each request.\nNow we are chatting it would be great to get to know you a bit better. Can you help me update my knowledge of you?\n\nSend a PATCH request with the 3 people from all time that you would invite to a dinner party. The body of your PATCH request should be an x-www-form-encoded, with a key value of 'guests' and the value a comma separated list of dinner guests. You'll have to check the 'Content-Type' matches the body encoding as well...", apprentice.getName());
        } catch(Error err) {
            System.out.println(apprenticesHashMap.get(hash));
            res.setStatus(404);
            return "I can't find you in my little server brain 😬. Try again, or start again.";
        }
    }

    @PatchMapping(path = "/apprentices/{hash}", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    String patchChallenge(Guest guests, @PathVariable String hash, HttpServletResponse res) {
        try {
            Apprentice bernard = new Apprentice("Bernard");
            apprenticesHashMap.put("123", bernard);
            Apprentice apprentice = apprenticesHashMap.get(hash);
            apprentice.setGuests(guests.getGuests());
            String reply = String.format("Hay %s, what an interesting selection of guests!\nI wonder what %s and %s would make of each other! What would you like everyone to eat?\n\nCan you send your ideal menu as a GET request with a set of query parameters to '/apprentices/{Your-Id}/menus'.\nThe query needs to include 'starter', 'main' and 'dessert'. Remember you will have to URIencode the values of your dishes.", apprentice.getName(), apprentice.getGuests()[0], apprentice.getGuests()[1]);
            return reply;
        } catch(Error err) {
            res.setStatus(404);
            return "I can't find you in my little server brain 😬. Try again, or start again.";
        }        
    }

    @GetMapping("/apprentices/{hash}/menus")
    String queryParamsChallenge(@PathVariable String hash, @RequestParam String starter, @RequestParam String main, @RequestParam String dessert, HttpServletResponse res) {
        try {
            Apprentice apprentice = apprenticesHashMap.get(hash);
            String reply = String.format("What a party %s! I can imagin %s enjoying %s followed by %s and finally %s will be a lot of fun!\n\nWell done. You have successfully completed a series of HTTP requests, using different methods, request parameters, different body encodings and encoded query parameters.\n\nAll of this forms a very important basis for really understanding REST and user authentication systems.", apprentice.getName(), String.join(", ", apprentice.getGuests()), starter, main, dessert);
            return reply;
        } catch (Error err) {
            res.setStatus(404);
            return "🧑🏾‍💻 Are the query params ok? Try again, or start again.";            
        }
    }
}
