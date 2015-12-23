/*
 * Copyright 2010-2013 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.killbill.billing.invoice.dao;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

import org.killbill.billing.callcontext.InternalTenantContext;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.util.entity.dao.EntitySqlDao;
import org.killbill.billing.util.entity.dao.EntitySqlDaoStringTemplate;

@EntitySqlDaoStringTemplate
public interface InvoiceSqlDao extends EntitySqlDao<InvoiceModelDao, Invoice> {

    @SqlQuery
    List<InvoiceModelDao> getInvoicesBySubscription(@Bind("subscriptionId") final String subscriptionId,
                                                    @BindBean final InternalTenantContext context);

    @SqlQuery
    UUID getInvoiceIdByPaymentId(@Bind("paymentId") final String paymentId,
                                 @BindBean final InternalTenantContext context);

    @SqlQuery
    List<InvoiceModelDao> getInvoicesByParentAccount(@Bind("parentAccountId") final UUID parentAccountId,
                                                     @BindBean final InternalTenantContext context);

    @SqlQuery
    List<InvoiceModelDao> getInvoicesByParentAccountDateRange(@Bind("parentAccountId") final UUID parentAccountId,
                                                              @Bind("startDate") final Date startDate,
                                                              @Bind("endDate") final Date endDate,
                                                              @BindBean final InternalTenantContext context);
}

