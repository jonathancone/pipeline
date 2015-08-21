/*
 * Copyright (c) 2015 Rebound Contributors
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
 */

package rebound.sql;

import rebound.util.Tuples;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcone on 8/12/15.
 */
public class Sql {
    private DataSource dataSource;
    private SqlParser sqlParser;

    public Sql(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Sql(DataSource dataSource, SqlParser sqlParser) {
        this.dataSource = dataSource;
        this.sqlParser = sqlParser;
    }

    protected Connection getConnection() {
        return Jdbc.getConnection(dataSource);
    }

    protected SqlParser getSqlParser() {
        if (sqlParser == null) {
            sqlParser = new StreamingSqlParser();
        }
        return sqlParser;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> T query(String sql, Handler<PreparedStatement, T> handler) {
        T t = null;
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            t = handler.handle(pstmt);
        } catch (SQLException e) {
            Thrower.rethrow(e);
        }
        return t;
    }

    public <T> List<T> query(String sql, final Handler<ResultSet, T> handler, final Object... parameters) {
        return query(sql, new Handler<PreparedStatement, List<T>>() {
            @Override
            public List<T> handle(PreparedStatement pstmt) throws SQLException {
                if (Tuples.isNotEmpty(parameters)) {
                    for (int i = 0; i < parameters.length; i++) {
                        pstmt.setObject(i + 1, parameters[i]);
                    }
                }

                try (ResultSet rs = pstmt.executeQuery()) {
                    List<T> list = new ArrayList<>();

                    while (rs.next()) {
                        list.add(handler.handle(rs));
                    }

                    return list;
                }
            }
        });
    }
}