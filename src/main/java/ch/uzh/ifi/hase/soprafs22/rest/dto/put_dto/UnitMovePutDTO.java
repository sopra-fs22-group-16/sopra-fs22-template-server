package ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto;

import ch.uzh.ifi.hase.soprafs22.rest.dto.PositionDTO;

public class UnitMovePutDTO {
    private PositionDTO start;
    private PositionDTO destination;

    public PositionDTO getStart() {
        return start;
    }

    public void setStart(PositionDTO start) {
        this.start = start;
    }

    public PositionDTO getDestination() {
        return destination;
    }

    public void setDestination(PositionDTO destination) {
        this.destination = destination;
    }
}
