/*
 * Copyright 2010-2013 Ning, Inc.
 * Copyright 2014-2015 Groupon, Inc
 * Copyright 2014-2015 The Billing Project, LLC
 *
 * The Billing Project licenses this file to you under the Apache License, version 2.0
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.joda.time.LocalDate;
import org.killbill.billing.callcontext.InternalCallContext;
import org.killbill.billing.callcontext.InternalTenantContext;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.invoice.InvoiceDispatcher.FutureAccountNotifications;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.InvoiceApiException;
import org.killbill.billing.util.entity.Pagination;
import org.killbill.billing.util.entity.dao.EntityDao;

public interface InvoiceDao extends EntityDao<InvoiceModelDao, Invoice, InvoiceApiException> {

    void createInvoice(final InvoiceModelDao invoice, final List<InvoiceItemModelDao> invoiceItems,
                       final boolean isRealInvoice, final FutureAccountNotifications callbackDateTimePerSubscriptions,
                       final InternalCallContext context);

    public void setFutureAccountNotificationsForEmptyInvoice(final UUID accountId, final FutureAccountNotifications callbackDateTimePerSubscriptions,
                                                             final InternalCallContext context);

    List<InvoiceItemModelDao> createInvoices(final List<InvoiceModelDao> invoices, final InternalCallContext context);

    InvoiceModelDao getByNumber(Integer number, InternalTenantContext context) throws InvoiceApiException;

    List<InvoiceModelDao> getInvoicesByAccount(InternalTenantContext context);

    List<InvoiceModelDao> getInvoicesByAccount(LocalDate fromDate, InternalTenantContext context);

    List<InvoiceModelDao> getInvoicesBySubscription(UUID subscriptionId, InternalTenantContext context);

    Pagination<InvoiceModelDao> searchInvoices(String searchKey, Long offset, Long limit, InternalTenantContext context);

    UUID getInvoiceIdByPaymentId(UUID paymentId, InternalTenantContext context);

    List<InvoicePaymentModelDao> getInvoicePayments(UUID paymentId, InternalTenantContext context);

    List<InvoicePaymentModelDao> getInvoicePaymentsByAccount(InternalTenantContext context);

    BigDecimal getAccountBalance(UUID accountId, InternalTenantContext context);

    BigDecimal getAccountCBA(UUID accountId, InternalTenantContext context);

    List<InvoiceModelDao> getUnpaidInvoicesByAccountId(UUID accountId, @Nullable LocalDate upToDate, InternalTenantContext context);

    // Include migrated invoices
    List<InvoiceModelDao> getAllInvoicesByAccount(InternalTenantContext context);

    InvoicePaymentModelDao postChargeback(UUID paymentId, BigDecimal amount, Currency currency, InternalCallContext context) throws InvoiceApiException;

    InvoiceItemModelDao doCBAComplexity(InvoiceModelDao invoice, InternalCallContext context) throws InvoiceApiException;

    Map<UUID, BigDecimal> computeItemAdjustments(final String invoiceId,
                                                 final Map<UUID, BigDecimal> invoiceItemIdsWithNullAmounts,
                                                 final InternalTenantContext context) throws InvoiceApiException;

    /**
     * Create a refund.
     *
     * @param paymentId                 payment associated with that refund
     * @param amount                    amount to refund
     * @param isInvoiceAdjusted         whether the refund should trigger an invoice or invoice item adjustment
     * @param invoiceItemIdsWithAmounts invoice item ids and associated amounts to adjust
     * @param transactionExternalKey    transaction refund externalKey
     * @param context                   the call callcontext
     * @return the created invoice payment object associated with this refund
     * @throws InvoiceApiException
     */
    InvoicePaymentModelDao createRefund(UUID paymentId, BigDecimal amount, boolean isInvoiceAdjusted, Map<UUID, BigDecimal> invoiceItemIdsWithAmounts,
                                        String transactionExternalKey, InternalCallContext context) throws InvoiceApiException;

    BigDecimal getRemainingAmountPaid(UUID invoicePaymentId, InternalTenantContext context);

    UUID getAccountIdFromInvoicePaymentId(UUID invoicePaymentId, InternalTenantContext context) throws InvoiceApiException;

    List<InvoicePaymentModelDao> getChargebacksByAccountId(UUID accountId, InternalTenantContext context);

    List<InvoicePaymentModelDao> getChargebacksByPaymentId(UUID paymentId, InternalTenantContext context);

    InvoicePaymentModelDao getChargebackById(UUID chargebackId, InternalTenantContext context) throws InvoiceApiException;

    /**
     * Retrieve am external charge by id.
     *
     * @param externalChargeId the external charge id
     * @return the external charge invoice item
     * @throws InvoiceApiException
     */
    InvoiceItemModelDao getExternalChargeById(UUID externalChargeId, InternalTenantContext context) throws InvoiceApiException;

    /**
     * Retrieve a credit by id.
     *
     * @param creditId the credit id
     * @return the credit invoice item
     * @throws InvoiceApiException
     */
    InvoiceItemModelDao getCreditById(UUID creditId, InternalTenantContext context) throws InvoiceApiException;

    /**
     * Delete a CBA item.
     *
     * @param accountId     the account id
     * @param invoiceId     the invoice id
     * @param invoiceItemId the invoice item id of the cba item to delete
     */
    void deleteCBA(UUID accountId, UUID invoiceId, UUID invoiceItemId, InternalCallContext context) throws InvoiceApiException;

    void notifyOfPayment(InvoicePaymentModelDao invoicePayment, InternalCallContext context);

    /**
     * @param accountId the account for which we need to rebalance the CBA
     * @param context   the callcontext
     */
    public void consumeExstingCBAOnAccountWithUnpaidInvoices(final UUID accountId, final InternalCallContext context);

    List<InvoiceModelDao> getInvoicesByParentAccount(UUID parentAccountId, LocalDate startDate, LocalDate endDate, InternalTenantContext context);
}
