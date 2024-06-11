package umu.tds.chord.model.discount;

import java.time.LocalDateTime;
import java.util.Date;

import umu.tds.chord.model.User;
import umu.tds.chord.model.discount.DiscountFactory.Type;
import umu.tds.chord.utils.DateConversor;

public class ElderDiscount extends Discount {

	protected ElderDiscount(Date start, Date end) {
		super(start, end);
	}
	
	protected ElderDiscount() {
		super();
	}

	@Override
	public boolean aplicable(User u) {
		return DateConversor.covertToLocalDateTime(u.getBirthday()).plusYears(65).isBefore(LocalDateTime.now());
	}

	@Override
	public double getDiscountFactor() {
		return 0.5;
	}

	@Override
	public Type getType() {
		return DiscountFactory.Type.ELDER;
	}
}
