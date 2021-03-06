/*
 * Copyright 2017, OpenSkywalking Organization All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Project repository: https://github.com/OpenSkywalking/skywalking
 */

package org.skywalking.apm.collector.storage.h2.base.dao;

import org.skywalking.apm.collector.client.h2.H2Client;
import org.skywalking.apm.collector.client.h2.H2ClientException;
import org.skywalking.apm.collector.storage.base.dao.AbstractDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 基于 H2 的 DAO 抽象类
 *
 * @author peng-yongsheng
 */
public abstract class H2DAO extends AbstractDAO<H2Client> {

    private final Logger logger = LoggerFactory.getLogger(H2DAO.class);

    public H2DAO(H2Client client) {
        super(client);
    }

    /**
     * 获得指定表的指定字段的最大值
     *
     * @param tableName 表名
     * @param columnName 字段
     * @return 最大值
     */
    protected final int getMaxId(String tableName, String columnName) {
        String sql = "select max(" + columnName + ") from " + tableName;
        return getIntValueBySQL(sql);
    }

    /**
     * 获得指定表的指定字段的最小值
     *
     * @param tableName 表名
     * @param columnName 字段
     * @return 最小值
     */
    protected final int getMinId(String tableName, String columnName) {
        String sql = "select min(" + columnName + ") from " + tableName;
        return getIntValueBySQL(sql);
    }

    private int getIntValueBySQL(String sql) {
        H2Client client = getClient();
        try (ResultSet rs = client.executeQuery(sql, null)) {
            if (rs.next()) {
                int id = rs.getInt(1);
                if (id == Integer.MAX_VALUE || id == Integer.MIN_VALUE) { // TODO 这个是为啥
                    return 0;
                } else {
                    return id;
                }
            }
        } catch (SQLException | H2ClientException e) {
            logger.error(e.getMessage(), e);
        }
        return 0;
    }

}
