package at.fhtw.mctg.model;

/**
 * Represents the status of a trade in the trading system.
 *
 * This enum defines the possible states a trade can be in during its lifecycle:
 * - PENDING: Indicates that the trade is awaiting completion or action.
 * - ACCEPTED: Indicates that the trade has been successfully completed and accepted.
 *
 * The TradeStatus enum is used throughout the application to manage and track
 * the current state of trades.
 */
public enum TradeStatus {
    PENDING,
    ACCEPTED
}
