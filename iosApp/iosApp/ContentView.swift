import SwiftUI
import shared

struct ContentView: View {
    @State var greet = "waiting"
    
    var body: some View {
        Text(greet).onAppear {
            GreetingIos(greeting: Greeting()).greeting().subscribe { result in
                if let result = result {
                    self.greet = result as String
                }
            } onComplete: {
                self.greet = "finished"
            } onThrow: { error in
                self.greet = "error"
                error.printStackTrace()
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
