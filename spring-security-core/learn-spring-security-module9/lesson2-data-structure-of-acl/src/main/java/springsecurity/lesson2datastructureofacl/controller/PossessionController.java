package springsecurity.lesson2datastructureofacl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springsecurity.lesson2datastructureofacl.persistence.entity.Possession;
import springsecurity.lesson2datastructureofacl.service.PossessionService;

@Controller
@RequestMapping("/possessions")
public class PossessionController {

    private final PossessionService service;

    public PossessionController(PossessionService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Possession view(@PathVariable Long id) {
        Possession p = new Possession();
        p.setId(id);
        return service.read(p);
    }
}
