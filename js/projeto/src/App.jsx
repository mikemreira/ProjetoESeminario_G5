import { useState } from 'react'
import { RouterProvider, createBrowserRouter } from 'react-router-dom'
import './App.css'
import SignUp from './login/SignUp'
import Home from './Home'
import LogIn from './login/LogIn'
import Success from './Success'
import Obras from './obras/Obras'

const router = createBrowserRouter([
  {
      "path": "/",
      "element": <Home/>,
  },
  {
      "path": "/login",
      "element": <LogIn />
  },
  {
    "path": "/signup",
    "element": <SignUp/>
  },
  {
    "path": "/success",
    "element": <Success/>
  },
  {
        "path": "/obras",
        "element": <Obras/>
    }
])

function App() {
  const [count, setCount] = useState(0)

  return (
    <RouterProvider router={router}>
      <Home/>
    </RouterProvider>
    /*
    <>
      <div>
        <SignUp></SignUp>
      </div>
    </>
    */
  )
}

export default App
