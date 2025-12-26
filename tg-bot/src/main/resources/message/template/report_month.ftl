📊 <b>ВАШ БАЛАНС ЗА МЕСЯЦ</b>

💰 Баланс: <b>${data.balance!''}</b>
<#if (data.hasIncomeCategories!false)>
    📈 Доходы: <b>+${data.income!''}</b>
    <tg-spoiler>
        <#list data.incomeCategories as category>
            • ${category.name}: <b>+${category.formattedAmount}</b> (${category.percentage})
        </#list>
    </tg-spoiler>
<#else>
    📈 Доходы: <b>+${data.income!''}</b>
</#if>
<#if (data.hasExpenseCategories!false)>
    📉 Расходы: <b>-${data.expense!''}</b>
    <tg-spoiler>
        <#list data.expenseCategories as category>
            • ${category.name}: <b>-${category.formattedAmount}</b> (${category.percentage})
        </#list>
    </tg-spoiler>
<#else>
    📉 Расходы: <b>-${data.expense!''}</b>
</#if>

━━━━━━━━━━━━━━━━━
/balance - Показать общий баланс
/add_income - Добавить доход
/add_expense - Добавить расход