package io.freedriver.jsonlink.config.v2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Reads {@code startupActions}. Every {@code type} in a board's list is checked before any action
 * in the file is constructed. An unknown {@code type} refuses that board's whole list.
 */
public final class StartupActionsDeserializer extends JsonDeserializer<Map<String, List<MappingsAction>>> {
    @Override
    public Map<String, List<MappingsAction>> deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        JsonNode root = context.readTree(parser);
        if (root == null || root.isNull()) {
            return Map.of();
        }
        if (!root.isObject()) {
            throw JsonMappingException.from(parser, "startupActions must be an object keyed by board id");
        }

        Map<String, Class<? extends MappingsAction>> types = MappingsAction.registeredTypes();
        List<BoardList> pending = new ArrayList<>();
        Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            pending.add(readBoard(parser, entry.getKey(), entry.getValue(), types));
        }

        Map<String, List<MappingsAction>> loaded = new LinkedHashMap<>();
        for (BoardList board : pending) {
            List<MappingsAction> actions = new ArrayList<>(board.nodes().size());
            for (JsonNode node : board.nodes()) {
                actions.add(context.readTreeAsValue(node, MappingsAction.class));
            }
            loaded.put(board.boardId(), List.copyOf(actions));
        }
        return Collections.unmodifiableMap(loaded);
    }

    @Override
    public Map<String, List<MappingsAction>> getNullValue(DeserializationContext context) {
        return Map.of();
    }

    private static BoardList readBoard(
            JsonParser parser,
            String boardId,
            JsonNode listNode,
            Map<String, Class<? extends MappingsAction>> types) throws JsonMappingException {
        if (listNode == null || listNode.isNull()) {
            return new BoardList(boardId, List.of());
        }
        if (!listNode.isArray()) {
            throw JsonMappingException.from(
                    parser, "startupActions for board '" + boardId + "' must be a list");
        }
        List<JsonNode> nodes = new ArrayList<>();
        for (JsonNode action : listNode) {
            String type = logicalType(action);
            if (type == null || !types.containsKey(type)) {
                throw JsonMappingException.from(parser, unknownType(boardId, type));
            }
            nodes.add(action);
        }
        return new BoardList(boardId, nodes);
    }

    private static String logicalType(JsonNode action) {
        if (action == null || !action.isObject()) {
            return null;
        }
        JsonNode type = action.get("type");
        if (type == null || type.isNull() || !type.isTextual()) {
            return null;
        }
        String text = type.asText();
        return text.isBlank() ? null : text;
    }

    static String unknownType(String boardId, String type) {
        String shown = type == null || type.isBlank() ? "<missing>" : type;
        return "unknown MappingsAction type '" + shown + "' for board '" + boardId
                + "'; refusing the board's startupActions";
    }

    private record BoardList(String boardId, List<JsonNode> nodes) {}
}
