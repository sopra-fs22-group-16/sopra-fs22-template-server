package ch.uzh.ifi.hase.soprafs22.game.tiles.interfaces;

import java.util.Map;

public interface ITileBuilder {
    void setTileType(String tileType);
    void setTileVariant(String tileVariant);
    void setTileTraversability(Boolean traversable);
    void setTileTraversingCost(int traversingCost);
    void setTileUnit(Map<String, Object> unit);
}
