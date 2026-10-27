import SwiftUI
import KMPNativeCoroutinesAsync
import KMPObservableViewModelSwiftUI
import Shared

struct HomeView: View {
    @StateViewModel
    var viewModel = HomeViewModel(
        greeting: KoinDependencies().greeting.message
    )

    var body: some View {
        VStack(spacing: 16) {
            Text(viewModel.greeting)
                .font(.title2)

            Text("\(viewModel.count)")
                .font(.largeTitle)

            Button("Increment") {
                viewModel.increment()
            }
            .buttonStyle(.borderedProminent)
        }
        .padding(24)
    }
}
