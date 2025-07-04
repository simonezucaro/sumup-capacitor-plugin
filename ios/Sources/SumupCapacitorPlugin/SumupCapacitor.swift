import Foundation

@objc public class SumupCapacitor: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
