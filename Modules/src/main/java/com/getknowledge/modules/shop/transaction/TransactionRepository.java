package com.getknowledge.modules.shop.transaction;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("TransactionRepository")
public class TransactionRepository extends BaseRepository<Transaction> {
    @Override
    protected Class<Transaction> getClassEntity() {
        return Transaction.class;
    }

    @Override
    public void remove(Transaction entity) {
        //Транзакцию и связанные с ней элементы невозможно удалить из системы через API
    }
}
