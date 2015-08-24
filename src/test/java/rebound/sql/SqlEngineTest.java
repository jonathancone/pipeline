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

import org.junit.Test;
import rebound.sql.test.Employee;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by jcone on 8/16/15.
 */
public class SqlEngineTest extends AbstractDataSourceTest {
    private SqlEngine sqlEngine;

    @Test
    public void testGetDataSource() throws Exception {
        assertEquals(getDataSource(), new SqlEngine(getDataSource()).getDataSource());
    }

    @Test
    public void testQuery1() throws Exception {
        List<Employee> employees = sql()
                .query("     SELECT           " +
                                "   employeeId,    " +
                                "   firstName,     " +
                                "   lastName,      " +
                                "   active,        " +
                                "   departureDate, " +
                                "   hireDate,      " +
                                "   payrollId,     " +
                                "   salary         " +
                                " FROM             " +
                                "   Employee       ",
                        new ResultSetCommand<Employee>() {
                            public Employee perform(ResultSet rs) throws SQLException {
                                Employee employee = new Employee();
                                employee.setEmployeeId(rs.getInt("employeeId"));
                                employee.setFirstName(rs.getString("firstName"));
                                employee.setLastName(rs.getString("lastName"));
                                employee.setActive(rs.getBoolean("active"));
                                employee.setDepartureDate(rs.getDate("departureDate"));
                                employee.setHireDate(rs.getDate("hireDate"));
                                employee.setPayrollId(rs.getInt("payrollId"));
                                employee.setSalary(rs.getBigDecimal("salary"));
                                return employee;
                            }
                        });

        for (Employee e : employees) {
            System.out.println(e.getFirstName() + e.getLastName());
        }

    }

    private SqlEngine sql() {
        if (sqlEngine == null) {
            sqlEngine = new SqlEngine(getDataSource());
        }
        return sqlEngine;
    }
}