# Peach Finance Manager
An android finance management app with Plaid integration.

Allows user to specify budgetting categories and log transactions appropriately, while tracking account balance.

# Demo

<img src="https://github.com/OliverMadine/peach-finance-manager/blob/main/docs/demo.gif" width="30%" height="30%"/>

# Usage - Emulator

Start a local plaid server by running `./start_server.sh ${CLIENT_ID} ${SECRET}` in the project root directory, using an appropriate plaid `CLIENT_ID` and `SECRET`. 

Then build with your favourite Android Virtual Device.

# Project structure

`financeManager.FinanceActivity` - Manages user interface of budgetting and transaction logging.
`financeManager.FinancePresenter` - User input validation and program logic for finance management.
`financeManager.FinanceSqlHelper` - SQLite database calls.

`linkNetwork` - Local server for Plaid link (note in prodcution this would not be used).

`plaidLink.linkActivity` - Bank account linking using plaid.

# Credit
Build using the [Plaid link android sdk](https://github.com/plaid/plaid-link-android).

Logo design: [streamifystore](https://streamifystore.com).
