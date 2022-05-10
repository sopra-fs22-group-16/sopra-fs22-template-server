package ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto;

import ch.uzh.ifi.hase.soprafs22.game.TurnInfo;

import java.util.List;

/**
 * Delta to be sent through web socket about the game so client can process info easier.
 */
public class GameDeltaPutDTO {
    private UnitCommandPutDTO move;

    private TurnInfo turnInfo;
    private List<HealthPutDTO> health;


    public UnitCommandPutDTO getMove() {
        return move;
    }

    public void setMove(UnitCommandPutDTO move) {
        this.move = move;
    }

    public TurnInfo getTurnInfo() {
        return turnInfo;
    }

    public void setTurnInfo(TurnInfo turnInfo) {
        this.turnInfo = turnInfo;
    }

    public List<HealthPutDTO> getHealth() {
        return health;
    }

    public void setHealth(List<HealthPutDTO> health) {
        this.health = health;
    }
}
