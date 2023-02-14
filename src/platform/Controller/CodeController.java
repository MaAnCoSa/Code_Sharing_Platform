package platform.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import platform.Model.Code;
import platform.Model.CodeReceiver;
import platform.Service.CodeService;

import java.util.List;

@RestController
@CrossOrigin
public class CodeController {

    // Reference to the code service object.
    @Autowired
    private CodeService codeService;

    // Constructor
    @Autowired
    public CodeController(CodeService codeService) {
        this.codeService = codeService;
    }

    // Gives the HTML page to get a specific snippet by its ID..
    @GetMapping(path = "/code/{id}", produces = "text/html")
    public String getCode(@PathVariable String id) {
        return codeService.getCode(id);
    }

    // Returns the specific snippet by its ID.
    @GetMapping(path = "/api/code/{id}", produces = "application/json;charset=UTF-8")
    public Code getCodeJson(@PathVariable String id) {
        return codeService.getCodeJson(id);
    }

    // Posts a new snippet into the system.
    @PostMapping("/api/code/new")
    public String postNewCode(@RequestBody CodeReceiver newCode) {
        return codeService.postNewCode(newCode);
    }

    // Gives the HTML page to post a new snippet into the system.
    @GetMapping("/code/new")
    public String getCodeNew() {
        return codeService.getCodeNew();
    }

    // Returns the last 10 snippets that have no restrictions.
    @GetMapping("/api/code/latest")
    public ResponseEntity<List<Code>> getLatestJson() {
        return codeService.getLatestJson();
    }

    // Gives the HTML page to get the last 10 snippets that have no restrictions.
    @GetMapping("/code/latest")
    public String getLatest() {
        return codeService.getLatest();
    }
}
