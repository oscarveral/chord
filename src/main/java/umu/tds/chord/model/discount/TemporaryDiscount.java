package umu.tds.chord.model.discount;

import java.time.LocalDateTime;
import java.util.Date;

import umu.tds.chord.model.User;
import umu.tds.chord.model.discount.DiscountFactory.Type;
import umu.tds.chord.utils.DateConversor;

public class TemporaryDiscount extends Discount {

	protected TemporaryDiscount(Date start, Date end) {
		super(start, end);
	}
	
	protected TemporaryDiscount() {
		super(new Date(), DateConversor.convertToDate(LocalDateTime.now().plusMonths(3)));
	}
	
	@Override
	public boolean aplicable(User u) {
		return DateConversor.covertToLocalDateTime(getEnd()).isAfter(LocalDateTime.now()) &&
				DateConversor.covertToLocalDateTime(getStart()).isBefore(LocalDateTime.now());
	
	}

	@Override
	public double getDiscountFactor() {
		return 0.8;
	}

	@Override
	public Type getType() {
		return Type.TEMPORARY;
	}
}
