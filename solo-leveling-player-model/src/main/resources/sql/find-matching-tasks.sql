WITH input_combinations AS (
    SELECT
        combination.player_task_id,
        combination.rarity,
        combination.topics
    FROM jsonb_to_recordset(?::jsonb) AS combination(
        player_task_id uuid,
        rarity int,
        topics int[]
    )
),
task_topic_agg AS (
    SELECT
        task_id,
        array_agg(topic ORDER BY topic) AS sorted_topics
    FROM task_topic_items
    GROUP BY task_id
),
input_players AS (
    SELECT
        player_task_id,
        rarity,
        topics,
        ROW_NUMBER() OVER (
            PARTITION BY rarity, topics
            ORDER BY player_task_id
        ) AS player_rank
    FROM input_combinations
),
matching_tasks AS (
    SELECT
        t.rarity AS rarity_ordinal,
        tta.sorted_topics AS topics_ordinals,
        t.id AS task_id,
        ROW_NUMBER() OVER (
            PARTITION BY t.rarity, tta.sorted_topics
            ORDER BY t.id
        ) AS task_rank
    FROM tasks t
    JOIN task_topic_agg tta ON tta.task_id = t.id
    WHERE t.version <> 0
      AND EXISTS (
        SELECT 1
        FROM input_players ip
        WHERE ip.rarity = t.rarity
          AND ip.topics = tta.sorted_topics
    )
    AND NOT EXISTS (
        SELECT 1
        FROM player_tasks pt
        WHERE pt.player_id = ?
          AND pt.task_id = t.id
    )
)
SELECT
    ip.player_task_id,
    mt.task_id
FROM input_players ip
JOIN matching_tasks mt ON
    mt.rarity_ordinal = ip.rarity
    AND mt.topics_ordinals = ip.topics
    AND mt.task_rank = ip.player_rank;