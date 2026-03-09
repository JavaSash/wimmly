📋 <b>Найденные транзакции</b>

<#if data.data.transactions?has_content>
    <#list data.data.transactions as tx>
        #${tx.displayId} [${tx.formattedDate}] ${tx.category}: ${tx.amount} <#if tx.comment??>💬 ${tx.comment}</#if>
    </#list>
<#else>
    😕 Транзакций не найдено
</#if>

━━━━━━━━━━━━━━━━━
/balance - Показать общий баланс
/add_income - Добавить доход
/add_expense - Добавить расход
/search_transactions - новый поиск
/delete_transaction - удалить транзакцию