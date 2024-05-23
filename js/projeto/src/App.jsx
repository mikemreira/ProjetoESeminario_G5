import { Outlet, Route, RouterProvider, createBrowserRouter, createRoutesFromElements } from 'react-router-dom'
import SignUp from './SignUp'
import Home from './Home'
import LogIn from './LogIn'
import './App.css'
import Success from './Success'
import NavBar from './NavBar'

const AppLayout = () => {
  return (
  <>
    <NavBar/>
    <Outlet/>
  </>
  )
}

const router = createBrowserRouter([
  {
    "path": "/",
    "element": <AppLayout/>,
    "children": [
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
      }
  ]}
])

function App() {

  return (
    
      <RouterProvider router={router}>
      
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
