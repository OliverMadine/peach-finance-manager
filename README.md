# Peach - Finance Manager
An Android finance management app with [Plaid](https://plaid.com) integration.

# Demo

<img alt="Peach demo" src="https://github.com/OliverMadine/peach-finance-manager/blob/main/docs/demo.gif" width="30%" height="30%"/>

# Getting Started

Start a local plaid server by running `./start_server.sh ${CLIENT_ID} ${SECRET}` in the project root directory, using an appropriate plaid `CLIENT_ID` and `SECRET`. 

Then build with your favourite Android Virtual Device (AVD).

# Project Structure

`financeManager.FinanceActivity` - Manages user interface of budgetting and transaction logging.

`financeManager.FinancePresenter` - User input validation and program logic for finance management.

`financeManager.FinanceSqlHelper` - SQLite database calls.

`linkNetwork` - Local server for Plaid link (Note: In prodcution this would not be used).

`plaidLink.linkActivity` - Bank account linking using plaid.

# Credit
Built using the [Plaid link android sdk](https://github.com/plaid/plaid-link-android).

Logo design: [streamifystore](https://streamifystore.com).
