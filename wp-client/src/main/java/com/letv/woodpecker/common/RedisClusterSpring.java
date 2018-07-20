package com.letv.woodpecker.common;



import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisNode;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by guoyang on 17/3/21.
 */
@Data
public class RedisClusterSpring extends RedisClusterConfiguration
{

    private Integer maxRedirects;

    RedisClusterSpring(String nodes)
    {
        Set<RedisNode> clusterNodes = null;

        if(StringUtils.isNotEmpty(nodes))
        {
            clusterNodes = new LinkedHashSet<RedisNode>();

            String item[] = nodes.split(";");

            for(int i = 0;i < item.length;i++)
            {
                String host[] = item[i].split(":");

                String ip = host[0];

                int port = Integer.parseInt(host[1]);

                RedisClusterNode redisClusterNode = new RedisClusterNode(ip,port);

                clusterNodes.add(redisClusterNode);
            }
        }
        super.setClusterNodes(clusterNodes);
    }
}
