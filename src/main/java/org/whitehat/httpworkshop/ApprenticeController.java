package org.whitehat.httpworkshop;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class ApprenticeController {

    private static Map<String, Apprentice> apprenticeNames = new HashMap<>();
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String gettingStarted() {
        return "Welcome. You have made your first successful GET request!\nTo continue through this HTTP request challenge I will ask you to make a series of HTTP requests.\nOnce you have completed them all you will unlock a special reward.\n\nTo get started can you make a POST request to `/apprentices`.\n Include your name in the body of the request as a stringified JSON object with the property 'name'";
    }

    @PostMapping("/apprentices")
    @ResponseStatus(HttpStatus.CREATED)
    String createApprenticeEntry(@RequestBody Apprentice apprentice, HttpServletResponse res) {
        String hashkey = apprentice.toString().split("@")[1];
        
        apprenticeNames.put(hashkey, apprentice);

        res.addHeader("Your-Id", hashkey);
        
        return String.format("Hi %s, can you see in the headers of this response I have placed a field called 'Your-Id'.\n\nCan you make a GET request to '/apprentices' with 'Your-Id' set as a parameter?", apprentice.getName());
    }

    @RequestMapping("/apprentices/{hash}")
    String withParams(@PathVariable String hash, HttpServletResponse res) {
        try {
            Apprentice apprentice = apprenticeNames.get(hash);
            return String.format("Nice work %s, I can see you know what you are doing! Keep using 'Your-Id' as a parameter.\nI am a stateless server so everything I need to talk to you has to be contained in each request.\nNow we are chatting it would be great to get to know you a bit better. Can you help me update my knowledge of you?\n\nSend a PATCH request with the 3 people from all time that you would invite to a dinner party. The body of your PATCH request should be an x-www-form-encoded, comma separated list. You'll have to update the 'Content-Type' as well...", apprentice.getName());
        } catch(Error err) {
            res.setStatus(404);
            return "I can't find you in my little server brain 😬. Try again, or start again.";
        }
    }

    @PatchMapping(path = "/apprentices/{hash}", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    String patchChallenge(Guest guests, @PathVariable String hash, HttpServletResponse res) {
        try {
            Apprentice bernard = new Apprentice("Bernard");
            apprenticeNames.put("123", bernard);
            Apprentice apprentice = apprenticeNames.get(hash);
            String reply = String.format("Hay %s, what an interesting selection of guests! I wonder what %s and %s would make of each other.", apprentice.getName(), guests.getGuests()[0], guests.getGuests()[1]);
            return reply;
        } catch(Error err) {
            res.setStatus(404);
            return "I can't find you in my little server brain 😬. Try again, or start again.";
        }        
    }
}
