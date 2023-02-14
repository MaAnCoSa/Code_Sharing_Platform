package platform.Service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import platform.Model.Code;
import platform.Model.CodeReceiver;
import platform.Repositories.CodeRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CodeService {

    // Reference to the code repository object.
    @Autowired
    private CodeRepository codeRepo;

    // Shows the HTML page to get a snippet by a specific ID.
    public String getCode(String id) {
        // If the snippet doesn't exist...
        if (!codeRepo.existsById(id)) {
            // ...throw a NOT FOUND exception.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        // We get the snippet from the repository.
        Code code = codeRepo.findById(id).get();
        // If the snippet has a restriction...
        if (code.isRestrictedTime() || code.isRestrictedView()) {
            // ...check if it needs to actually be deleted or just updated
            // on its remaining time and/or views.
            updateRestrictions(code);
        }
        // Return the HTML page with the code snippet.
        String response =
                """
                        <html>
                            <head>
                                <title>Code</title>
                                <link rel="stylesheet"
                                    href="//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css">
                                <script src="//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js"></script>
                                <script>hljs.initHighlightingOnLoad();</script>
                            </head>
                            <body>
                        """;
        // This shows the creation date for the code snippet.
        response += "<span id=\"load_date\">" + code.getDate().toString() + "</span>\n";
        // If the snippet has a time restriction...
        if (code.isRestrictedTime()) {
            // ...it is shown.
            response += "<span id=\"time_restriction\">" + code.getTime() + "</span>\n";
        }
        // If the snippet has a view restriction...
        if (code.isRestrictedView()) {
            // ...it is shown.
            response += "<span id=\"views_restriction\">" + code.getViews() + "</span>\n";
        }
        // This shows the code snippet itself.
        response += "<pre id=\"code_snippet\"><code>" + code.getCode() + "</code></pre>\n";
        response +=
                """
                    </body>
                    </html>
                """;

        return response;
    }

    // To get the code snippet by its ID.
    public Code getCodeJson(String id) {
        // If the snippet doesn't exist...
        if (!codeRepo.existsById(id)) {
            // ...throw a NOT FOUND exception.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        // We get the snippet fro the repository.
        Code code = codeRepo.findById(id).get();
        // If the snippet has a restriction...
        if (code.isRestrictedTime() || code.isRestrictedView()) {
            // ...check if it needs to actually be deleted or just updated
            // on its remaining time and/or views.
            updateRestrictions(code);
        }

        return code;
    }

    // Posts a new code snippet to the system.
    public String postNewCode(CodeReceiver newCode) {
        // We create a new code snippet object.
        Code code = new Code();
        // All its properties are setted.
        code.setId(UUID.randomUUID().toString());
        code.setCode(newCode.getCode());
        code.setDateNoFormat(LocalDateTime.now());
        code.setDate(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS").format(code.getDateNoFormat()));
        code.setTime(newCode.getTime());
        code.setViews(newCode.getViews());
        code.setRestrictedTime(code.getTime() != 0);
        code.setRestrictedView(code.getViews() != 0);
        // The snippet is saved into the repository.
        codeRepo.save(code);
        // The generated ID is returned in JSON format.
        return "{\"id\":\"" + code.getId() + "\"}";
    }

    // Shows the HTML page to post a new code snippet into the system.
    public String getCodeNew() {
        // Returns the HTML page.
        return
            """
                    <html>
                        <head>
                            <title>Create</title>
                            <link rel="stylesheet"
                                href="//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css">
                            <script src="//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js"></script>
                            <script>hljs.initHighlightingOnLoad();</script>
                            <script>
                                function send() {
                                    let object = {
                                        "code": document.getElementById("code_snippet").value
                                    };
                                                    
                                    let json = JSON.stringify(object);
                                                    
                                    let xhr = new XMLHttpRequest();
                                    xhr.open("POST", '/api/code/new', false)
                                    xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');
                                    xhr.send(json);
                                                    
                                    if (xhr.status == 200) {
                                        alert("Success!");
                                    }
                                }
                            </script>
                        </head>
                        <body>
                            <form>
                                <textarea id="code_snippet"> ... </textarea>
                                <input id="time_restriction" type="text"/>
                                <input id="views_restriction" type="text"/>
                                <button id="send_snippet" type="submit" onclick="send()">Submit</button>
                            </form>
                        </body>
                    </html>
                    """;
    }

    // Returns the 10 latest snippets that have no restrictions on them.
    public ResponseEntity<List<Code>> getLatestJson() {
        // Gets the list of the 10 snippets.
        List<Code> list = latest10();
        // Returns the list in the response.
        return ResponseEntity.ok(list);
    }

    // Shows the HTML page with the 10 latest snippets that have no restrictions on them.
    public String getLatest() {
        // Gets the list of the 10 snippets.
        List<Code> list = latest10();
        // Returns the HTML page to see the snippets.
        String response =
                        """
                                <html>
                                    <head>
                                        <title>Latest</title>
                                        <link rel="stylesheet"
                                            href="//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css">
                                        <script src="//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js"></script>
                                        <script>hljs.initHighlightingOnLoad();</script>
                                    </head>
                                    <body>
                                """;
        // For each of the 10 snippets...
        for (int i = 0; i < list.size(); i++) {
            // ...its date of creation is shown...
            response += "<span id=\"load_date\">" + list.get(i).getDate().toString() + "</span>\n";
            // ...and the code itself is shown.
            response += "<pre id=\"code_snippet\"><code>" + list.get(i).getCode() + "</code></pre>\n";
        }
        response +=
                    """
                        </body>
                        </html>
                    """;

        return response;
    }


    // To get the list of the 10 latest code snippets that have no restrictions on them.
    public List<Code> latest10() {
        // Gets a list of all non-restricted snippets.
        List<Code> list = codeRepo.findAllByRestrictedTimeAndRestrictedView(false, false);
        // Sorts the list and gets only the 10 first snippets.
        List<Code> sortedList = list.stream().sorted(Comparator.comparing(Code::getDate).reversed()).limit(10).toList();
        // Returns the list in the correct sorting.
        return sortedList.stream().sorted(Comparator.comparing(Code::getDate).reversed()).toList();
    }

    // Updates the time and view counters if needed (or deletes the snippet).
    private void updateRestrictions(Code code) {
        // If the snippet had a view restriction...
        if (code.isRestrictedView()) {
            // ...if the snippets has been viewed its last time...
            if (code.getViews() == 0) {
                // ...the snippet is deleted.
                codeRepo.deleteById(code.getId());
                // A NOT FOUND exception is thrown.
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            // The view counter gets updated.
            code.setViews(code.getViews() - 1);
        }
        // If the snippet had a time restriction...
        if (code.isRestrictedTime()) {
            // ...The time left is calculated.
            long timeLeft = LocalDateTime.now()
                    .until(code.getDateNoFormat().plusSeconds(code.getTime()), ChronoUnit.SECONDS);
            // If the time left is over (negatives)...
            if (timeLeft <= 0) {
                // ...the snippets get deleted.
                codeRepo.deleteById(code.getId());
                // A NOT FOUND exception is thrown.
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            // The time counter is updated.
            code.setTime(timeLeft);
        }
        // The code snippet is updated into the repository.
        codeRepo.save(code);
    }
}
