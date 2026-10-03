package org.omar.dto;


import org.omar.model.Actor;

public record ActorDto(Short id, String firstName, String lastName) {

    public static ActorDto from(Actor actor) {
        return new ActorDto(actor.getId(), actor.getFirstName(), actor.getLastName());
    }
}