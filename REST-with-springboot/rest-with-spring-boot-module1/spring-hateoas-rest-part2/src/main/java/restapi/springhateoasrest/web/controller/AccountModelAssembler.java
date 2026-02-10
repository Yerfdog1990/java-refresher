package restapi.springhateoasrest.web.controller;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import restapi.springhateoasrest.web.dto.AccountDto;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AccountModelAssembler implements RepresentationModelAssembler<AccountDto, EntityModel<AccountDto>> {

    @Override
    public EntityModel<AccountDto> toModel(AccountDto entity) {
        EntityModel<AccountDto> dtoModel = EntityModel.of(entity);
        dtoModel.add(linkTo(methodOn(AccountModelAssemblerApi.class).readOne(entity.getId())).withSelfRel());
        dtoModel.add(linkTo(methodOn(AccountModelAssemblerApi.class).readAll()).withRel(IanaLinkRelations.COLLECTION));
        dtoModel.add(linkTo(methodOn(AccountModelAssemblerApi.class).deposit(entity.getId(), null)).withRel("Deposit"));
        dtoModel.add(linkTo(methodOn(AccountModelAssemblerApi.class).withdraw(entity.getId(), null)).withRel("Withdrawal"));
        return dtoModel;
    }
}
