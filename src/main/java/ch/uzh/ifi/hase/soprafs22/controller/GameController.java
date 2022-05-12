package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.game.Position;
import ch.uzh.ifi.hase.soprafs22.game.units.commands.AttackCommand;
import ch.uzh.ifi.hase.soprafs22.game.units.commands.MoveCommand;
import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.UnitAttackPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.web_socket.GameDeltaWebSocketDTO;
import ch.uzh.ifi.hase.soprafs22.game.units.Unit;
import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.HealthPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.UnitMovePutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.web_socket.UnitMoveWebSocketDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Game Controller
 * This class is responsible for handling all REST request that are related to
 * the game.
 * The controller will receive the request and delegate the execution to the
 * GameService and finally return the result.
 */

@RestController
public class GameController {
    @Value("${api.version}")
    private String apiVersion;

    private static final String TOPIC_GAME = "/topic/game/";

    @Autowired
    SimpMessagingTemplate socketMessage;

    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PutMapping("/{apiVersion}/game/match/{id}/command/attack")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unitAttack(@RequestHeader("token") String token, @PathVariable Long id, @RequestBody UnitAttackPutDTO attackPutDTO) {

        AttackCommand attackCommand = DTOMapper.INSTANCE.convertUnitAttackPutDTOToAttackCommand(attackPutDTO);

        List<Unit> affectedUnits = gameService.unitAttack(id, token, attackCommand);
        // Movement delta for socket.
        UnitMovePutDTO moveDeltaSock = new UnitMovePutDTO();
        moveDeltaSock.setStart(attackPutDTO.getAttacker());
        moveDeltaSock.setDestination(attackPutDTO.getAttackerDestination());

        GameDeltaWebSocketDTO deltaSock = new GameDeltaWebSocketDTO();
        deltaSock.setMove(moveDeltaSock);
        List<HealthPutDTO> healthDTOs = new ArrayList<>();
        for (Unit unit : affectedUnits){
            // Health delta for socket.
            HealthPutDTO health = new HealthPutDTO();
            health.setHealth(unit.getHealth());
            health.setUnitPosition(DTOMapper.INSTANCE.convertPositionToPositionDTO(unit.getPosition()));
            healthDTOs.add(health);
        }
        if (healthDTOs.size() > 0)
            deltaSock.setHealth(healthDTOs);

        sendThroughSocket(id, deltaSock);
    }

    @PutMapping("/{apiVersion}/game/match/{id}/command/move")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unitMove(@RequestHeader("token") String token, @PathVariable Long id, @RequestBody UnitMovePutDTO movePutDTO) {
        MoveCommand moveCommand = DTOMapper.INSTANCE.convertUnitMovePutDTOToMoveCommand(movePutDTO);

        Unit movedUnit = this.gameService.unitMove(id, token, moveCommand);

        UnitMoveWebSocketDTO unitMoveWebSocketDTO = DTOMapper.INSTANCE.convertUnitToUnitMoveWebSocketDTO(movedUnit);
        unitMoveWebSocketDTO.setStart(movePutDTO.getStart());

        this.socketMessage.convertAndSend(TOPIC_GAME + id, unitMoveWebSocketDTO);
    }

    /**
     * All socket info should be sent through this method to ensure format consistency.
     */
    private void sendThroughSocket(long id, GameDeltaWebSocketDTO gameDelta) {
        this.socketMessage.convertAndSend("/topic/game/" + id, gameDelta);
    }
}
